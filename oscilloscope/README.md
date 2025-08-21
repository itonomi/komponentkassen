# Oscilloscope - Electric Clojure Component Gallery

A storybook-like component gallery for Electric Clojure development. Display and organize your Electric components in a visual, searchable interface.

## Features

- **Component Gallery**: Visual display of Electric Clojure components
- **Category Organization**: Group components by category (Buttons, Forms, Layout, etc.)
- **Search Functionality**: Filter components by name
- **Clean UI**: Modern sidebar navigation with component cards

## Getting Started

### Development

```shell
# Start the development server
clj -A:dev -X dev/-main

# Or via REPL
clj -A:dev
# Then at the REPL: (dev/-main)
```

The app will start on http://localhost:8080

### Project Structure

- `src/com/itonomi/oscilloscope.cljc` - Main Oscilloscope component
- `src/electric_starter_app/main.cljc` - Example usage with sample components

## Usage

To use Oscilloscope in your project, import it and provide a collection of components:

```clojure
(ns your-app
  (:require [com.itonomi.oscilloscope :as oscilloscope]))

(e/defn YourComponent []
  ;; Your Electric component code
  )

(e/defn Main []
  (oscilloscope/Oscilloscope
   {:components [{:id "unique-id"
                  :title "Component Name"
                  :description "Component description"
                  :category "Category Name"
                  :Component YourComponent}]}))
```

Note the uppercase `:Component` key - this follows Electric Clojure conventions where uppercase indicates an Electric function.

## License

Part of the Komponentkassen project.