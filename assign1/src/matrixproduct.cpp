#include <cmath>
#include <cstdlib>
#include <iomanip>
#include <iostream>
#include <stdio.h>
#include <time.h>

#if HAS_PAPI
#include <papi.h>
#endif

using namespace std;

#define setup()                                                                \
    timespec start, end;                                                       \
                                                                               \
    int i, j, k;                                                               \
                                                                               \
    auto a = new double[size_a * size_a];                                      \
    auto b = new double[size_b * size_b];                                      \
    auto c = new double[size_a * size_b];                                      \
                                                                               \
    for (i = 0; i < size_a; i++)                                               \
        for (j = 0; j < size_a; j++)                                           \
            a[i * size_a + j] = 1.0;                                           \
                                                                               \
    for (i = 0; i < size_b; i++)                                               \
        for (j = 0; j < size_b; j++)                                           \
            b[i * size_b + j] = (double)(i + 1);                               \
                                                                               \
    for (i = 0; i < size_a; i++)                                               \
        for (j = 0; j < size_b; j++)                                           \
            c[i * size_b + j] = 0.0;                                           \
                                                                               \
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &start);

#define cleanup()                                                              \
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &end);                             \
    printf("Time: %.8f seconds\n", diff_timespec(&end, &start));              \
                                                                               \
    cout << "Result matrix: " << endl;                                         \
    for (j = 0; j < min(10, size_b); j++)                                      \
        cout << c[j] << " ";                                                   \
    cout << endl;                                                              \
                                                                               \
    delete[] a;                                                                \
    delete[] b;                                                                \
    delete[] c;

double diff_timespec(const timespec *time1, const timespec *time0) {
    return (time1->tv_sec - time0->tv_sec) +
           (time1->tv_nsec - time0->tv_nsec) / 1000000000.0;
}

void mult(int size_a, int size_b) {
    setup();

    // num da linha
    for (i = 0; i < size_a; i++)
        // pos na coluna
        for (j = 0; j < size_b; j++)
            // ajudante para percorrer coluna e linha
            for (k = 0; k < size_a; k++)
                // o que fica na posiçao
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j];

    cleanup();
}

void mult_line(int size_a, int size_b) {
    setup();

    // linha
    for (i = 0; i < size_a; i++)
        // ajudante
        for (k = 0; k < size_a; k++)
            // coluna
            for (j = 0; j < size_b; j++)
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j];

    cleanup();
}

void mult_block(int size_a, int size_b, int size_block) {
    setup();

    int ib, kb, jb;
    // aqui devemos tratar blocos com elementos da matriz
    int num_side_blocks = size_a / size_block;

    for (ib = 0; ib < num_side_blocks; ib++)
        for (kb = 0; kb < num_side_blocks; kb++)
            for (jb = 0; jb < num_side_blocks; jb++)
                // linha
                for (i = ib * size_block; i < (ib + 1) * size_block; i++)
                    // ajudante
                    for (k = kb * size_block; k < (kb + 1) * size_block; k++)
                        // coluna
                        for (j = jb * size_block; j < (jb + 1) * size_block;
                             j++)
                            c[i * size_a + j] +=
                                a[i * size_a + k] * b[k * size_b + j];

    cleanup();
}

int main(int argc, char *argv[]) {
#if HAS_PAPI
    int event_set = PAPI_NULL;
    long long values[2];

    if (PAPI_library_init(PAPI_VER_CURRENT) != PAPI_VER_CURRENT)
        cerr << "FAIL" << endl;

    if (PAPI_create_eventset(&event_set) != PAPI_OK)
        cerr << "ERROR: create event set" << endl;

    if (PAPI_add_event(event_set, PAPI_L1_DCM) != PAPI_OK)
        cerr << "ERROR: PAPI_L1_DCM" << endl;

    if (PAPI_add_event(event_set, PAPI_L2_DCM) != PAPI_OK)
        cerr << "ERROR: PAPI_L2_DCM" << endl;
#endif

    int op = 1;
    int lines, cols, size_block;
    do {
        cout << endl << "1. Multiplication" << endl;
        cout << "2. Line Multiplication" << endl;
        cout << "3. Block Multiplication" << endl;
        cout << "Selection?: ";
        cin >> op;
        if (op == 0)
            break;
        printf("Dimensions: lins=cols ? ");
        cin >> lines;
        cols = lines;

#if HAS_PAPI
        values[0] = 0;
        values[1] = 0;

        // Start counting
        if (PAPI_start(event_set) != PAPI_OK)
            cerr << "ERROR: Start PAPI" << endl;
#endif

        switch (op) {
        case 1:
            mult(lines, cols);
            break;
        case 2:
            mult_line(lines, cols);
            break;
        case 3:
            cout << "Block Size? ";
            cin >> size_block;
            mult_block(lines, cols, size_block);
            break;
        }

#if HAS_PAPI
        if (PAPI_stop(event_set, values) != PAPI_OK)
            cerr << "ERROR: Stop PAPI" << endl;

        printf("L1 DCM: %lld \n", values[0]);
        printf("L2 DCM: %lld \n", values[1]);

        if (PAPI_reset(event_set) != PAPI_OK)
            cerr << "FAIL reset" << endl;
#endif

    } while (op != 0);

#if HAS_PAPI
    if (PAPI_remove_event(event_set, PAPI_L1_DCM) != PAPI_OK)
        cerr << "FAIL remove event" << endl;

    if (PAPI_remove_event(event_set, PAPI_L2_DCM) != PAPI_OK)
        cerr << "FAIL remove event" << endl;

    if (PAPI_destroy_eventset(&event_set) != PAPI_OK)
        cerr << "FAIL destroy" << endl;
#endif
}