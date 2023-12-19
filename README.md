# Quarkus Http Reverse Proxy - experimental

A non-caching http/s proxy based on Quarkus and Vert.x that can easily be extended with authentication mechanisms.

## Example Configuration

```yaml
server:
  default-charset: utf-8
  paths:
    - path: /users
      proxy:
        remote-uri: https://random-data-api.com/api/v2/users
    - path: /geoip
      proxy:
        remote-uri: https://api.techniknews.net/ipgeo/
    - path: /wiki
      proxy:
        remote-uri: https://ja.wikipedia.org/wiki
    - path: /static
      static:
        directory: src/test/resources
        index: index.html
  errors:
    - status-code: 400-499
      template:  default-default-4xx.html

```

## TODOs

* [ ] Fix HTTP error handling
* [ ] Keep alive
* [ ] Filter headers
* [ ] Add static headers
* [ ] Custom HTTP error response
* [ ] TLS settings for outgoing requests
* [ ] Set http version for outgoing requests
* [ ] Hostname verification (on/off)
* [ ] Regex path mapping
