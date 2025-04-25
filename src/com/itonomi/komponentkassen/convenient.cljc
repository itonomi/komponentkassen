(ns com.itonomi.komponentkassen.convenient
  "Components built on top of the `com.itonomi.komponentkassen.core`
  components. These components make some assumptions abour your use
  case and expose a higher-level API than the hooked componets API of kk.core.

  The goal is to trade control for convenience.

  \"In the beginning you want results, in the end you want control.\"

  Well, here's both!"
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]
            [com.itonomi.komponentkassen.core :as kk]
            [lambdaisland.deep-diff2 :as ddiff]))

;; Is the primitive underlying `Registration-page` and `Login-page` a
;; general `Credentials-form`? I think there's an underlying
;; state-machine to be reused.
;;
;; - `:submit`
;; - `:change-credentials`
;;
;; They have the same events.
;;
;; {:error nil
;;  :username/error nil
;;  :password/error nil
;;  :username ""
;;  :password ""}
;;
;; They have the same state datums.
#_#_

(e/defn Registration-page
  "A high quality fully managed end-to-end user registration page.

Register must be an Electric function like

(e/fn Register [{:as credentials :keys [username password]}] ...)

which attempts to register the given user to your system. If it fails,
it must return credentials with :error, :username/error (inclusive-)
or :password/error assoced to an error message of your choice.

If passed, Invalidate must be an Electric function like

(e/fn Invalidate [{:as credentials :keys [username password]}] ...)

which may eagerly invalidates the given credentials by
associng :username/error or :password/error."
  [{:as config :keys [application-name]} Register Invalidate]
  (let [!state (atom {:error nil
                      :username/error nil
                      :password/error nil
                      :username ""
                      :password ""})
        reset! (partial reset! !state)]
    (Credentials-form-page
     (kk/Registration-form config
                           state
                           [[:submit * (kk/Fn->fr Register)]
                            [:change-credentials * (kk/Fn->fr Invalidate)]]
                           reset!))))

(e/defn Authentication-page
  "A high quality fully managed end-to-end user authentication page.

Authenticate must be an Electric function like

(e/fn Authenticate [{:as credentials :keys [username password]}] ...)

which attempts to authenticate the given user to your system. If it
fails, it must return (assoc credentials :error your-error-message)."
  [{:as config :keys [application-name]} Authenticate]
  (let [!state (atom {:error nil
                      :username/error nil
                      :password/error nil
                      :username ""
                      :password ""})
        reset! (partial reset! !state)
        Submit-login-form-fr (e/fn Submit-login-form-fr [[old new]]
                               [old (Authenticate (:username new) (:password new))])
        Validate-credentials-fr (e/fn Validate-credentials-fr [[old new]]
                                  [old (merge new (validate-credentials new))])]
    (Credentials-form-page
     (kk/Authentication-form config
                             state
                             [[:submit * Submit-login-form-fr]
                              [:change-credentials * Validate-credentials-fr]]
                             reset!))))
