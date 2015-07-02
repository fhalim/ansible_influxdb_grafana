#!/bin/sh
AUTH="root:root"
#curl -u ${AUTH} -X DELETE "http://localhost:8086/db/riemann-data"
curl -u ${AUTH} -X POST 'http://localhost:8086/db' -d '{"name": "riemann-data"}'
curl -u ${AUTH} -X POST 'http://localhost:8086/db/riemann-data/users' -d '{"name": "riemann", "password": "password"}'
curl -u ${AUTH} -X POST 'http://localhost:8086/db' -d '{"name": "grafana"}'
curl -u ${AUTH} -X POST 'http://localhost:8086/db/grafana/users' -d '{"name": "grafana", "password": "password"}'
