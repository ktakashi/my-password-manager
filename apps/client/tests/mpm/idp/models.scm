(import (rnrs)
	(mpm idp models)
	(srfi :19)
	(srfi :64))

(test-begin "Client - IdP models")

(define response-json '#(("pseudonym" . "value")))
(define error-response-json
  '#(("type" . "type") ("timestamp" . "2023-03-15T01:01:01.000Z")))

(test-assert (authentication-request?
	      (make-authentication-request "user-id" "password")))
(test-equal '#(("userId" . "user-id") ("password" . "passwd"))
	    (authentication-request->json
	     (make-authentication-request "user-id" "passwd")))

(test-assert (registration-request?
	      (make-registration-request "user-id" "password")))
(test-equal '#(("userId" . "user-id") ("password" . "passwd"))
	    (registration-request->json
	     (make-registration-request "user-id" "passwd")))

(test-assert (authentication-response?
	      (json->authentication-response response-json)))
(test-equal "value"
	    (authentication-response-pseudonym
	     (json->authentication-response response-json)))

(test-assert (error-response?
	      (json->error-response error-response-json)))
(let ((er (json->error-response error-response-json)))
  (test-equal 'type (error-response-type er))
  (test-assert (date? (error-response-timestamp er))))

(test-end)
(exit (test-runner-fail-count (test-runner-current)))
