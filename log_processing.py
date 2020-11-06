import sys, os

if sys.argv[1] == '-s':
    TSs = []
    TJs = []
    lc = 0
    with open(sys.argv[2]) as f:
        for line in f:
            _, TS, _, TJ = line.split(",")
        TSs.append(int(TS))
        TJs.append(int(TJ))
        lc += 1
    print()
    print("\t\t\t", sys.argv[2])
    print("average TS:\n\t", sum(TSs)/lc * 1E-6, "ms")
    print("average TJ:\n\t", sum(TJs)/lc * 1E-6, "ms")

else:
    for file in os.listdir(sys.argv[1]):
        TSs = []
        TJs = []
        lc = 0
        file = os.path.join(sys.argv[1], file)
        with open(file) as f:
            for line in f:
                _, TS, _, TJ = line.split(",")
            TSs.append(int(TS))
            TJs.append(int(TJ))
            lc += 1
        print()
        print("\t\t\t", file)
        print("average TS:\n\t", sum(TSs)/lc * 1E-6, "ms")
        print("average TJ:\n\t", sum(TJs)/lc * 1E-6, "ms")
