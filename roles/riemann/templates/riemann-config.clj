; vim: filetype=clojure

(logging/init {:file "/var/log/riemann/riemann.log" :console false})


(let [host "0.0.0.0"]
  (tcp-server {:host host})
  (udp-server {:host host})
  (ws-server {:host host})
  (repl-server {:host host})
  (graphite-server {:host host}))

;(instrumentation {:interval 1})

(periodically-expire 10)

(def print-sample (throttle 1 30 #(info %)))

; Base Influxdb configuration
(def influxdb-baseconfig {:host "{{inventory_hostname}}"
                          :port 8086
                          :username "riemann"
                          :password "password"
                          :db "riemann-data"})

(defn namedidb [name]
  (batch 500 1 (influxdb
    (merge influxdb-baseconfig {:series (fn [opts] name)}))))

; Write interesting events to InfluxDB

(defn sampled-action [action] #(do
  (action %)
  (print-sample %)))

(let [tm (namedidb "testmetrics")
      m (namedidb "metrics")]
  (streams
    (where (service #"^(?!(riemann ))")
      (where (service #"^test")
        (sampled-action tm)
       (else sampled-action m)
      )
    )
  )
)

; Index all non-internal events
(let [index (tap :index (index))]
  (streams
    (where (service #"^(?!(riemann ))")
      index
    )
  )
)
