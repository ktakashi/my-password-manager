(import (rnrs)
	(mpm context)
	(mpm configurations)
	(text yaml)
	(getopt))

(define (usage file)
  (print file " -c $config")
  (exit 1))

(define (main args)
  (with-args args
      ((config  (#\c "config") #t (usage (car args)))
       . ignore)
    (let ((v (car (call-with-input-file config yaml-read))))
      (configuration->execution-context (json->configuration v)))))
