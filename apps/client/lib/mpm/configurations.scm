#!nounbound
(library (mpm configurations)
    (export (rename (configuration <configuration>))
	    configuration? make-configuration
	    configuration-idp configuration-connection

	    (rename (service <service>))
	    service? make-service
	    service-base-uri service-endpoints
	    service-timeouts service-max-connection

	    (rename (connection <connection>))
	    connection-timeouts connection-max-connection-per-route
	    connection-time-to-live
	    
	    (rename (timeouts <timeouts>))
	    timeouts? make-timeouts
	    timeouts-read timeouts-connection
	    timeouts-connection-request timeouts-dns

	    json->configuration
	    )
    (import (rnrs)
	    (net uri)
	    (srfi :13 strings)
	    (text json pointer)
	    (text json object-builder)
	    (util duration))

;; Root configuration
(define-record-type configuration
  (fields idp				;; service
	  connection))

;; Service
(define-record-type service
  (fields base-uri
	  timeouts
	  max-connection
	  endpoints))			;; ((name path) ...)

(define-record-type timeouts
  (fields read
	  connection
	  connection-request
	  dns))

(define-record-type connection
  (fields timeouts max-connection-per-route time-to-live))

(define name-pointer (json-pointer "/name"))
(define path-pointer (json-pointer "/path"))
(define (object->endpoint obj)
  (let ((name (name-pointer obj))
	(path (path-pointer obj)))
    (cons (string->symbol name) path)))
(define (timeout->duration s)
  (define (->duration v u)
    (let ((n (string->number v)))
      (unless n (assertion-violation 'timeout->duration "Invalid format" s))
      (case (string->symbol u)
	((d) (duration:of-days n))
	((h) (duration:of-hours n))
	((m) (duration:of-minutes n))
	((s) (duration:of-seconds n))
	((ms) (duration:of-millis n))
	(else (assertion-violation 'timeout->duration "Invalid unit" u s)))))
  (cond ((string-index s (lambda (c) (not (char-numeric? c)))) =>
	 (lambda (i)
	   (->duration (substring s 0 i) (substring s i (string-length s)))))
	(else (->duration s "ms"))))

(define timeouts-builder
  (json-object-builder
   (make-timeouts
    (? "read" #f timeout->duration)
    (? "connection" #f timeout->duration)
    (? "connection-request" #f timeout->duration)
    (? "dns" #f timeout->duration))))

(define connection-builder
  (json-object-builder
   (make-connection
    (? "timeouts" #f timeouts-builder)
    (? "max-connection-per-route" #f)
    (? "time-to-live" #f timeout->duration))))

(define service-builder
  (json-object-builder
   (make-service
    ("base-uri" string->uri)
    (? "timeouts" #f timeouts-builder)
    (? "max-connection" #f)
    (? "endpoints" '() (@ list object->endpoint)))))

(define configuration-builder
  (json-object-builder
   (make-configuration
    ("idp" service-builder)
    (? "connection" #f connection-builder))))

(define (json->configuration json)
  (json->object json configuration-builder))

)
