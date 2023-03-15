#!nounbound
(library (mpm idp models)
    (export (rename (authentication-request <authentication-request>))
	    authentication-request? make-authentication-request
	    
	    (rename (authentication-response <authentication-response>))
	    authentication-response?
	    authentication-response-pseudonym

	    (rename (registration-request <registration-request>))
	    registration-request? make-registration-request

	    (rename (credential-holder-user-id authentication-request-user-id)
		    (credential-holder-password authentication-request-password)
		    (credential-holder-user-id registration-request-user-id)
		    (credential-holder-password registration-request-password))
	    authentication-request->json
	    json->authentication-response
	    registration-request->json
	    
	    (rename (error-response <error-response>))
	    error-response?
	    error-response-type error-response-timestamp
	    
	    json->error-response
	    
	    )
    (import (rnrs)
	    (srfi :19 time)
	    (text json object-builder))
(define-record-type credential-holder
  (fields user-id password))
;; request and response
(define-record-type authentication-request
  (parent credential-holder))
(define-record-type authentication-response
  (fields pseudonym))
(define-record-type registration-request
  (parent credential-holder))

(define-record-type error-response
  (fields type timestamp))

(define credential-holder-serializer
  (json-object-serializer
   (("userId" credential-holder-user-id)
    ("password" credential-holder-password))))

(define (authentication-request->json ar)
  (object->json ar credential-holder-serializer))
(define (registration-request->json rr)
  (object->json rr credential-holder-serializer))


(define authentication-response-builder
  (json-object-builder
   (make-authentication-response
    "pseudonym")))

(define (json->authentication-response json)
  (json->object json authentication-response-builder))

;; TODO handle fraction seconds...
(define (iso-string->date s) (string->date s "~Y-~m-~dT~H:~M:~S~z"))
(define error-response-builder
  (json-object-builder
   (make-error-response
    ("type" string->symbol)
    ("timestamp" iso-string->date))))
(define (json->error-response json) (json->object json error-response-builder))
)
