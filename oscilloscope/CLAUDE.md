# How To Write Electric Clojure Correctly

## Always USE e/diff-by When Using e/for With a Collection

INCORRECT:

(e/for [message ["Hey" "How are you?"]]
  (dom/div (dom/text message)))
  
CORRECT:

(e/for [message (e/diff-by identity ["Hey" "How are you?"])]
  (dom/div (dom/text message)))
  
REASON:

e/for natively works with Electric tables. e/diff-by is the way to
access a collection as an Electric table.

## Never Use e/server Or e/client From Within a Regular Clojure Function

INCORRECT:

(fn [] (e/server (make-a-pizza)))

CORRECT:

(e/server (make-a-pizza))

REASON:

This is simply invalid and will fail to compile.

## NEVER Use dom/class

INCORRECT:

(dom/class "my-awesome-class")

CORRECT:

(dom/props {:class ["my-awesome-class"]})

REASON:

dom/class does not exist

## Do Not Pass Callbacks to Electric Functions Unless You Specifically Know That It Takes Callbacks

INCORRECT:

(MyLovelyElectricFunction
  {:on-click DoSomething} 
  (e/fn [] (dom/text "Hello")))
						  
CORRECT:

(MyLovelyElectricFunction
  {:on-click DoSomething} 
  (e/fn []
    (let [[t err] (e/Token (dom/On "click" identity nil))]
      (when t
	    (case (swap! !path conj k) ; replace (swap! ...) with your side effect
	      (t))))))
		  
REASON:

React-style callbacks are not commonly used in Electric
Clojure. dom/On is the native way to handle events.

Remember that events propagate. Therefore you can typically choose
whether to handle a given event in a child component or parent
component.

## Do Not deref Atoms; Watch Them Instead

INCORRECT:

(MyLovelyComponent @!my-thing)

CORRECT:

(MyLovelyComponent (e/watch !my-thing))

REASON: When !my-thing changes, we want the change to automatically
propagate to MyLovelyComponent. That will only happen if we e/watch
the atom!

## Never Try Starting The App Yourself; Ask The User to Start it