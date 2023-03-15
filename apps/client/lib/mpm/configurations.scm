#!nounbound
(library (mpm configurations)
    (export (rename (configuration <configuration>))
	    configuration? make-configuration
	    configuration-idp

	    (rename (service <service>))
	    service? make-service
	    service-base-uri service-endpoints

	    build-configuration
	    )
    (import (rnrs)
	    (net uri)
	    (text json pointer)
	    (text json object-builder))

;; Root configuration
(define-record-type configuration
  (fields idp				;; service
	  ))

;; Service
(define-record-type service
  (fields base-uri
	  endpoints))			;; ((name path) ...)

(define name-pointer (json-pointer "/name"))
(define path-pointer (json-pointer "/path"))
(define (object->endpoint obj)
  (let ((name (name-pointer obj))
	(path (path-pointer obj)))
    (cons (string->symbol name) path)))

(define service-builder
  (json-object-builder
   (make-service
    ("base-uri" string->uri)
    (? "endpoints" '() (@ list object->endpoint)))))

(define configuration-builder
  (json-object-builder
   (make-configuration
    ("idp" service-builder))))

(define (build-configuration json)
  (json->object json configuration-builder))

)
