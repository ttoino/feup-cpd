from time import clock_gettime, CLOCK_PROCESS_CPUTIME_ID


def mult(size_a: int, size_b: int):
    a = [1.0] * size_a * size_a
    b = [i + 1 for i in range(size_b) for j in range(size_b)]
    c = [0] * size_a * size_b

    start = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)

    for i in range(size_a):
        for j in range(size_b):
            for k in range(size_a):
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j]

    end = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)
    print(f"Time: {end - start:3.3f} seconds\n")

    print("Result matrix: " + " ".join(map(str, c[:min(10, size_b)])))


def mult_line(size_a: int, size_b: int):
    a = [1.0] * size_a * size_a
    b = [i + 1 for i in range(size_b) for j in range(size_b)]
    c = [0] * size_a * size_b

    start = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)

    for i in range(size_a):
        for k in range(size_a):
            for j in range(size_b):
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j]

    end = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)
    print(f"Time: {end - start:3.3f} seconds\n")

    print("Result matrix: " + " ".join(map(str, c[:min(10, size_b)])))


def main():
    op = 1

    while op:
        print("1. Multiplication")
        print("2. Line Multiplication")
        # print("3. Block Multiplication")
        op = int(input("Selection?: "))

        if op == 0:
            break

        lines = cols = int(input("Dimensions: lins=cols ? "))

        if op == 1:
            mult(lines, cols)

        if op == 2:
            mult_line(lines, cols)

        # if op == 3:
        #     size_block = int(input("Block Size? "))
        #     mult_block(lines, cols, size_block);


if __name__ == "__main__":
    main()
