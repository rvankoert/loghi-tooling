#!/bin/bash

curl -v -X POST -F "identifier=test" -F "xml=@files/extract_baselines/NL-HaNA_1.04.02_2144_0414.xml" -F "mask=@files/extract_baselines/NL-HaNA_1.04.02_2144_0414.png" -F "invertImage=false" -F "laypa_config=@files/extract_baselines/laypa_config.yaml" http://localhost:8080/extract-baselines

result_file="/tmp/upload/test/NL-HaNA_1.04.02_2144_0414.xml"

sleep 5

if [ -f $result_file ]; then
  printf "\n result exists\n"

  number_of_baselines=$(cat < $result_file | grep -c "<Baseline")

  rm $result_file

  if [ "$number_of_baselines" -eq 42 ]; then
    exit 0
  else
    printf " %s is not equal to the expected number of baselines 42\n" "$number_of_baselines"
    exit 1
  fi

fi

printf "\n result does not exist\n"
exit 1

