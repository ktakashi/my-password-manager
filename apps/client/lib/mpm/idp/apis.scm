#!nounbound
(library (mpm idp apis)
    (export idp-error?
	    idp-error-response

	    authenticate
	    register)
    (import (rnrs)
	    (mpm context)
	    (mpm idp models)
	    (net http-client)
	    (rfc base64)
	    (sagittarius crypto digests)
	    (srfi :197 pipeline)
	    (text json)
	    (util concurrent))

(define-condition-type &idp-error &error
  make-idp-error idp-error?
  (response idp-error-response))

(define (authenticate context user-id password)
  (let ((f (call-idp-credential-api context user-id password
	    (list 'authentication make-authentication-request authentication-request->json))))
    (future-map json->authentication-response f)))

(define (register context user-id password)
  (call-idp-credential-api context user-id password
   (list 'registration make-registration-request registration-request->json)))

(define (call-idp-credential-api context user-id password api)
  (let ((idp-password (password->idp-password user-id password))
	(endpoint (execution-context-service-endpoint
		   context 'idp (car api))))
    (define req ((cadr api) user-id idp-password))
    (define request
      (http:request-builder
       (uri endpoint)
       (method 'POST)
       (content-type "application/json")
       (body (credential-request->json-bytevector req (caddr api)))))
    (define http-client (execution-context-http-client context))
    (chain (http:client-send-async http-client request)
	   (future-map check-error _))))

(define (credential-request->json-bytevector request ->json)
  (let-values (((o e) (open-string-output-port)))
    (json-write/normalized (->json request) o)
    (string->utf8 (e))))

(define (password->idp-password user-id password)
  (define md (make-message-digest *digest:sha-256*))
  (message-digest-init! md)
  (message-digest-process! md (string->utf8 user-id))
  (message-digest-process! md (string->utf8 password))
  (utf8->string (base64-encode (message-digest-done md))))

(define (check-error response)
  (define status (string->number (http:response-status response)))
  (when (>= status 400)
    (let ((err (json->error-response (response->json response))))
      (raise (condition (make-idp-error err)
			(make-who-condition (error-response-type err))
			(make-message-condition "IdP error")
			(make-irritants-condition err)))))
  (or (= status 201)
      (<= 300 status <= 399) ;; TODO get Location header?
      (response->json response)))

(define (response->json resp)
  (json-read (open-string-input-port (utf8->string (http:response-body resp)))))
)
