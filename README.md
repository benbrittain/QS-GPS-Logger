QS-GPS-Logger
=============

Simple GPS Logger that sends data to arbitrary endpoint

## API
closely mirrors the Java Android API
```
{
  "locations": [
   {
      "timestamp": "1408912444000",
      "latitude" : "40.801063",
      "longitude": "-76.3419352",
      "speed"    : "0.75",
      "bearing"  : "165.7",
      "accuracy" : "17.0",
      "altitude" : "87.0"
    }
  ]
}
```


## Rationale
I have been frustrated be all sorts of "Quantified Self" apps that take possesion of
some of the most personal data I have. This allows me to know exactly where and what happens
with my data.This is a simple hack so that I can run statistical analysis of where I go and what I do. Hopefully I can correlate this with other data I have started collecting as well.

WARNING: This version does not use Https, it is trivial to add in, but unless you have a CA trusted
key you'll need to have android explicitly trust your key.

## Install

0. Be able to build an android app
1. make sure platform-tools/ as well as the tools/ directory is in your PATH environment variable.
2. cd qs-gps
3. ant build
4. adb push bin/qs-gps-logger /sdcard
5. install on phone

## Run Server
0. modify config/config.json
1. cd server
2. node server.js


## Contribution
please.

## License
MIT Licensed (c) 2014 Ben Brittain

Alternatively, do whatever.
