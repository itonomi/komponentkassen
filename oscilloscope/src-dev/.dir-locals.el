;; For Emacs users
;;
;; This just makes it so that jacking in with C-c M-j gives you a repl
;; right away rather than having to C-u C-c M-j and answer
;; "clojure-cli" followed by adding the :dev alias.
((clojurec-mode
  (cider-preferred-build-tool . clojure-cli)
  (cider-clojure-cli-aliases . ":dev")))
