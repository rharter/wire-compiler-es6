# Wire Compiler: EcmaScript 6

Protocol Buffer compiler, powered by [Wire](https://github.com/square/wire), that generates EcmasScript 6 compatible Javascript.

This currently has very limited support and validation.  It's really a proof of concept at this point, so use it at your own risk.

# Usage

Checkout the source and compile the jar.

```bash
./gradlew clean jar
```

Run the jar, providing a path to the directory containing your `.proto` files, and a directory to write the resutling `protos.js` file to.

```bash
java -jar build/libs/wire-compiler-es6-1.0-SNAPSHOT.jar --proto-path=/path/to/protos --js-out=.
```

The compiler will generate a single `protos.js` file containing classes for each Message in your `.proto` files.  The resulting classes can be used like any other, and will validate property values when writing as JSON, and only write known properties.

Compiling this message:

```proto
syntax = "proto3";
message Foo {
  string bar = 1;
  uint32 baz = 2; 
}
```

Will generate this `protos.js` file.

```javascript
module.exports.Foo = class Foo {
  constructor(bar, baz) {
    this.bar = bar
    this.baz = baz
  }
  
  static from(properties) {
    let bar = properties['bar']
    let baz = properties['baz']
    return new Foo(bar, baz)
  }
  
  validate() {
    if ((typeof this.bar) !== 'string' && (typeof this.bar) !== 'undefined') {
      throw Error('bar must be a string or undefined, but was ' + (typeof this.bar))
    }
    if ((typeof this.bar) !== 'number' && (typeof this.bar) !== 'undefined') {
      throw Error('bar must be a number or undefined, but was ' + (typeof this.bar))
    }
  }
  
  toJSON() {
    this.validate()
    return {
      bar: this.bar,
      baz: this.baz
    }
  }
}
```

You can then use the `protos.js` file using `require`:

```javascript
let protos = require('./protos.js')
let foo = new protos.Foo("bar", 1)
console.log(Json.stringify(foo))
```

# License

```
Copyright 2016 Ryan Harter.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```