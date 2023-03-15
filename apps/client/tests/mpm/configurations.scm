(import (rnrs)
	(mpm configurations)
	(net uri)
	(srfi :64))

(test-begin "Client - configurations")

(define config-json
  '#(("idp" . #(("base-uri" . "http://localhost:8080/")
		("endpoints"
		 #(("name" . "authentication")
		   ("path" . "/authenticate"))
		 #(("name" . "registration")
		   ("path" . "/register")))))))

(test-assert (configuration? (build-configuration config-json)))
(let ((config (build-configuration config-json)))
  (test-assert (service? (configuration-idp config)))
  (let ((uri (service-base-uri (configuration-idp config))))
    (test-assert (uri? uri))
    (test-equal "http://localhost:8080/" (uri->string uri)))
  (test-equal '((authentication . "/authenticate") (registration . "/register"))
	      (service-endpoints (configuration-idp config))))

(test-end)
(exit (test-runner-fail-count (test-runner-current)))
