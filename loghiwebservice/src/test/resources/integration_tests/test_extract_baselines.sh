#!/bin/bash

curl -v -X POST -F "identifier=test" -F "xml=@files/extract_baselines/NL-0400410000_26_005006_000381.xml" -F "mask=@files/extract_baselines/NL-0400410000_26_005006_000381.png" -F "invertImage=false" -F "laypa_config=@files/extract_baselines/laypa_config.yaml" -F "image=@files/extract_baselines/NL-0400410000_26_005006_000381.jpg" http://localhost:8080/extract-baselines

result_file="/tmp/upload/test/NL-0400410000_26_005006_000381.xml"
if [ "$1" ]; then
  result_file="$1/test/NL-0400410000_26_005006_000381.xml"
fi

sleep 5

if [ -f $result_file ]; then
  printf "\n result exists\n"

  number_of_baselines=$(cat < $result_file | grep -c "<Baseline")

  rm $result_file

  if [ "$number_of_baselines" -eq 105 ]; then
    printf " number of baselines is 105 like expected\n"
    exit 0
  else
    printf " %s is not equal to the expected number of baselines 105\n" "$number_of_baselines"
    exit 1
  fi

fi

printf "\n result does not exist\n"
exit 1

