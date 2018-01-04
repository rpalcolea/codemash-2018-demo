![](https://raw.githubusercontent.com/Netflix/vizceral/master/logo.png)

##### Using Docker
If you don't have a node environment setup or would like to run this example on a platform, there is a Dockerfile for experimental usage.

```
$ docker build -t rpalcolea/vizceral-demo .
```
```
$ docker run -p 41911:8080 -d rpalcolea/vizceral-demo
```

Then you should be able to navigate to http://localhost:41911
