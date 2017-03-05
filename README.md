# i18n-clj

This library is designed to retrieve and optionally format locale specific message strings from various resource bundles on the classpath. It currently supports \*.properties, \*.yaml or \*.yml, and \*.edn or \*.clj files

It contains a few different formatting functions depending on your preference. Currently it provides formatting methods for `java.util.MessageFormat` based formatting, standard java/clojure `format` based formatting, common list `format` based formatting (via `clojure.pprint/cl-format`), and finally moustache template formatting.  See examples below.

[![Clojars Project](https://img.shields.io/clojars/v/org.pupcus/i18n-clj.svg)](https://clojars.org/org.pupcus/i18n-clj)

## Bundles

A bundle is a collection of resource files for a given basename and extension/file type

    messages.properties
    messages_es.properties
    messages_de_DE.properties

or

    messages.edn
    messages_es.edn
    messages_de_DE.edn

### creating a resource bundle

Bundles are components that can be started via the Stuart Sierra component library.  On Startup, the components will initialize a reference map of bundles that map to locales based on the filetype defined in the formats vector.

    (component/start (org.pupcus.i18n.bundle.BundleManager. "messages" ["yml" "yaml"]))

Another way to create a resource bundle is by using a helper function.

    (def messages (bundle/create-bundle-manager basename [formats]))

where `basename` is a keyword or string that defines the base name of the files where the messages are stored and `[formats]` is a vector of string 'extensions' that define the format the files are in. Valid formats are edn (format vector should be `["edn" "clj"]`, `["edn"]` or `["clj"]`), yaml (format vector would be `["yml" "yaml"]`, `["yml"]` or `["yaml"]`), or properties (format vector would be `["properties"]`)

### lookup strings in resource bundles

Bundles implement `clojure.lang.IFn` and will look up the key in themselves:

    (bundle key)

or

    (bundle key locale)

exmaple


    (messages :planet)
    "Mars"

Note that the `key` can also be a vector of keys that traverse a nested data structure (uses `get-in` in the implementation) IF the underlying file type supports it. At this time only the edn and yaml bundles support this.

    (messages [:templates :format])

### Formatting messages

There is a util namespace and a moustache namespace that contain functions to format messages.  You can format a message in the following ways:

#### Java `MessageFormat`

    (require '[org.pupcus.i18n.utils :refer [message-format]])

    (message-format bundle key & args)
    (message-format bundle key locale & args)

#### java/clj c-style `format` strings

    (require '[org.pupcus.i18n.utils :refer [format]])

    (format bundle key & args)
    (format bundle key locale & args)

#### common lisp `format` (as implemented by `clojure.pprint/cl-format`)

    (require '[org.pupcus.i18n.utils :refer [cl-format])

    (cl-format bundle key & args)
    (cl-format bundle key locale & args)

#### moustache templating

    (require '[org.pupcus.i18n.moustache :refer [moustache])

    (moustache bundle key map)
    (moustache bundle key locale map)

NOTE that you cannot use the mustache option (yet?) in your moustache templates that changes the delimeters when using this helper function.  It requires the {{...}} delimeters in your string to work.  When formatting the `key`'s string with `moustache`, the names of the variables within the delimeters in the string are pulled out and are subsequently looked up as keys themselves in the property files and, when found, are merged with the map provied before rendering. Any key/values in the provided map take precedence over any that are found in the property files.

In all these examples the `key` is looked for in the bundle according to the `locale` (or the default locale) to get the 'formattable' string. This string is then formatted with args (or a map for moustache).

####Examples

Suppose you had the following in a file called `messages.properties`:

    planet = Mars

    # template string for java's MessageFormat formatting
    template.message = At {2,time,short} on {2,date,long}, we detected {1,number,integer} spaceships on the planet {0}.

    # template string for java/clojure format
    template.format = At %s on %s, we detected %d spaceships on the planet %s.

    # template string for common lisp format
    template.lisp = At ~A on ~A, we detected ~A spaceships on the planet ~A.

    # template string for moustache formatting
    template.moustache = At {{time}} on {{date}}, we detected {{\#ships}}{{ships}} spaceships{{/ships}}{{^ships}}nothing{{/ships}} on the planet {{planet}}.

The following are examples of each of the four 'formatting' functions available as applied to the above values:

    (let [d (doto (java.util.Date.) (.setTime 1426194140054))
        time (.format (java.text.SimpleDateFormat. "h:mm a") d)
        date (.format (java.text.SimpleDateFormat. "MMMMM dd, YYYY") d)]

        (message-format messages :template.message (messages :planet) 6 d))
    => "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."

        (format messages :template.format time date 6 (messages :planet)))
    => "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."

        (cl-format messages :template.lisp time date 6 (messages :planet)))
    => "At 5:02 PM on March 12, 2015, we detected 6 spaceships on the planet Mars."

        (moustache messages :template.moustache {:time time :date date}))
    => "At 5:02 PM on March 12, 2015, we detected nothing on the planet Mars."

Notice in the last exmple using moustache that the `ships` and `planet` keys were not in the map.  The keys in the format string were searched for in the messages bundle and `planet` was found.  The lack of `ships` anywhere triggered the appropriate sentence fragment to be used.  See the moustache docs for more about moustache.  See the tests for more examples.

### middleware

There is also a `with-i18n` ring middleware function that will try and determine the locale in various ways and set the locale for all calls made in the request.  You can override the determination by simply providing the locale argument yourself to any of the above functions.  The `with-i18n` function takes one optional key value parameter `:default` that sets the default locale for the request if no locale can be determined. If no `:default` is given it is set to the default locale of the server/host as determined by `java.util.Locale/getDefault`

    (-> app
        wrap-params
        wrap-keyword-params
        (with-i18n :default :de_DE)
        ...)


### `wrap-locale`

The `with-i18n` middleware uses `wrap-locale` to create a binding for a thread local var, `*locale*` for the duration of the request.  This function can also be used to override any portions of your code without having to pass the locale to all the concerned functions.

See the tests for examples.

### `refresh`

There is also a `refresh` function:

    (require '[org.pupcus.i18n.utils :refer [refresh])

    (refresh bundle)

Calling this function will cause the bundles to be reread from the source IF the source has changed.

## License

Distributed under the Eclipse Public License.
