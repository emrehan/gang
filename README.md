# gang
group messaging over threads

## Usage

```
lein figwheel
```

Wait for figwheel to finish and notify browser of changed files! Then in another terminal tab:

```
lein run
```

point browser to:
http://localhost:8080

Open up your browser console, which will log the data returned from the server when you click either of the 2 buttons for socket with/without callback (pull/push).


## Deploy to Heroku

To make Rente run on Heroku, you need to let Leiningen on Heroku use the "package" build task.

To do this, and point Leiningen on Heroku to the "package" target, add the following config variable to Heroku by running this command:

```
heroku config:add LEIN_BUILD_TASK=package
```

Everything is nicely wrapped in shiny purple foil if you simply click this button:

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Enjoy!

## License

Copyright © Enterlab 2014-2015

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
