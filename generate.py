import csv
import sys
import matplotlib.pyplot as plt
sats = dict()
test = list()

with open(sys.argv[1], mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    line_count = 0
    for row in csv_reader:
        if line_count == 0:
            print(f'Column names are {", ".join(row)}')
            line_count += 1
            continue
        if row["SATNAME"] not in sats:
           sats[row["SATNAME"]]= list()
        sats[row["SATNAME"]].append(row)
        #print(float(row["MEDIAN"]))
        
        line_count += 1

counter=0
for satname,rows in sats.items():
    apogee= list()
    time=list()
    for row in rows:
        time.append(float(row["TIMESINCELAUNCH"]))
        apogee.append(float(row["MEDIAN"]))
    if(len(time)==len(apogee)):
        counter+=1
        time.sort()
        plt.plot(time,apogee,linewidth=1)
print(counter)
plt.ylabel('Orbital Height (Median) in km')
plt.xlabel('Days since launch')
plt.title(sys.argv[3])
plt.savefig(sys.argv[2], bbox_inches='tight', dpi=400,)
