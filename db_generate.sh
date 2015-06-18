#!/bin/bash
#Created 2015-06-18
#Shell script to generate a fresh db for votebot
export JSON=`cat settings.json | tr -d '\n'`
DB_USER=`python -c "import json; print ($JSON[\"db-user\"])"`
DB_PASS=`python -c "import json; print ($JSON[\"db-pass\"])"`
DB_NAME=`python -c "import json; print ($JSON[\"db-name\"])"`
echo $DB_USER
echo $DB_PASS
echo $DB_NAME

