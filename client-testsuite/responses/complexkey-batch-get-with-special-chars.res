HTTP/1.1 200 OK
Content-Length: 552
Content-Type: application/json
X-RestLi-Protocol-Version: 1.0.0

{
  "statuses" : { },
  "results" : {
    "part1=key%21*%27%28%29%3B%3A%40%26%3D%2B%24%2C%2F%3F%23%5B%5D.%7E&part2=2&part3=APPLE" : {
      "message" : {
        "message" : "test message"
      },
      "key" : {
        "part1" : "key!*'();:@&=+$,/?#[].~",
        "part2" : 2,
        "part3" : "APPLE"
      }
    },
    "part1=two&part2=7&part3=ORANGE" : {
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
