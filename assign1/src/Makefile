HAS_PAPI := $(shell which papi_avail)

matrixproduct: matrixproduct.cpp
ifeq ($(strip $(HAS_PAPI)),)
	g++ matrixproduct.cpp -O2 -o matrixproduct
else
	g++ matrixproduct.cpp -O2 -D HAS_PAPI -lpapi -o matrixproduct
endif

.PHONY: run
run: matrixproduct
	./matrixproduct

.PHONY: run_py
run_py: matrixproduct.py
	python3 matrixproduct.py

.PHONY: run_pypy
run_pypy: matrixproduct.py
	pypy3 matrixproduct.py

.PHONY: clean
clean:
	rm -rf ./matrixproduct
