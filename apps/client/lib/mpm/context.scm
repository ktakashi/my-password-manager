#!nounbound
(library (mpm context)
    (export execution-context?
	    execution-context-http-client
	    execution-context-configuration
	    configuration->execution-context)
    (import (rnrs)
	    (mpm configurations)
	    (net http-client)
	    (srfi :19 time))
;; execution context holding configuration dependent objects, e.g. http-client
(define-record-type execution-context
  (fields http-client
	  configuration))

(define (duration->millis duration)
  (let ((sec (time-second duration))
	(nsec (time-nanosecond duration)))
    (+ (* sec 1000) (div nsec 1000000))))
(define (duration->second duration)
  (let ((sec (time-second duration))
	(nsec (time-nanosecond duration)))
    (+ sec (div nsec 1000000000))))

(define (configuration->execution-context (configuration configuration?))
  (define connection (configuration-connection configuration))
  (define timeouts (and connection (connection-timeouts connection)))
  (define pooling-config
    (and connection timeouts
	 (http-pooling-connection-config-builder
	  (connection-request-timeout
	   (duration->millis (timeouts-connection-request timeouts)))
	  (connection-timeout (duration->millis (timeouts-connection timeouts)))
	  (read-timeout (duration->millis (timeouts-read timeouts)))
	  (dns-timeout (duration->millis (timeouts-dns timeouts)))
	  (max-connection-per-route
	   (connection-max-connection-per-route connection))
	  (time-to-live
	   (duration->second (connection-time-to-live connection))))))
    (make-execution-context
   (http:client-builder
    (connection-manager
     (and pooling-config (make-http-pooling-connection-manager pooling-config)))
    (follow-redirects (http:redirect never)))
   configuration))

)
