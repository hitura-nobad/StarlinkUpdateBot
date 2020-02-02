import csv
import sys
import datetime
from pathlib import Path

print(len(sys.argv))
files = dict()
with open(sys.argv[1], mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file, delimiter=';')
    line_count = 0
    for row in csv_reader:
        if line_count == 0:
            line_count += 1
            continue
        
        if row["LAUNCH"] not in files:
            print(row["LAUNCH"])
            print(row)
            newfile = open ("starlink/output/"+row["LAUNCH"]+".csv",'a')
            if not Path("starlink/output/"+row["LAUNCH"]+".csv").is_file():
                newfile.write("SATNAME,APOGEE,PERIGEE,MEDIAN,INCLINATION,TIMESINCELAUNCH\n")
            files[row["LAUNCH"]]=newfile
        ney = files[row["LAUNCH"]]
        ney.write(row["SATNAME"]+","+row["APOGEE"]+","+row["PERIGEE"]+","+row["MEDIAN"]+","+row["INCLINATION"]+","+row["TIMESINCELAUNCH"]+"\n")
for name, file in files.items():
    file.close()
