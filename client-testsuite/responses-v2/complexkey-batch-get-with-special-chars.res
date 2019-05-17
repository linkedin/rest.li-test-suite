HTTP/1.1 200 OK
Content-Length: 333
Content-Type: application/json
X-RestLi-Protocol-Version: 2.0.0

{
  "statuses" : { },
  "results" : {
    "(part1:key!*%27%28%29;%3A@&=+$%2C/?#[].~,part2:2,part3:APPLE)" : {
      "message" : {
        "message" : "test message"
      },
      "key" : {
        "part1" : "key!*'();:@&=+$,/?#[].~",
        "part2" : 2,
        "part3" : "APPLE"
      }
    },
    "(part1:two,part2:7,part3:ORANGE)" : {
      "message" : {
        "message" : "test message"
      },
      "key" : {
        "part1" : "two",
        "part2" : 7,
        "part3" : "ORANGE"
      }
    }
  },
  "errors" : { }
}
