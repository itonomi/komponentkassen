#!/bin/bash
git clone https://github.com/navikt/aksel.git
rm -rf aksel/@navikt/aksel-icons/icons/*.yml
mv aksel/@navikt/aksel-icons/icons/*.svg .
rm -rf aksel
