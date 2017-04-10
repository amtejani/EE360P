#include <iostream>
#include <mpi.h>
#include <fstream>
#include <vector>
#include <cstdlib>
using namespace std;

int readMatrix(int* N, int* M, int** matrix) {
	vector<int> matrixVector;
	ifstream in("matrix.txt");
	int number;
	in >> number;
	*N = number;
	while(in >> number) {
		matrixVector.push_back(number);
	}
	in.close();
	*matrix = (int*) malloc(sizeof(int)*matrixVector.size());
	for(int i = 0; i < matrixVector.size(); i++) {
		(*matrix)[i] = matrixVector[i];
	}
	*M = matrixVector.size() / (*N);
}

int readVector(int** vect) {
	vector<int> vectorVector;
	ifstream in("vector.txt");
	int number;
	while(in >> number) {
		vectorVector.push_back(number);
	}
	in.close();
	*vect = (int*) malloc(sizeof(int)*vectorVector.size());
	for(int i = 0; i < vectorVector.size(); i++) {
		(*vect)[i] = vectorVector[i];
	}
}

int main(void) {
	MPI_Init(NULL, NULL);

	int world_rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
	int world_size;
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);


	int M,N;

	// size NxM
	int* matrix;
	// size M
	int* vector;
	// size N
	int* product;
	int* tempMatrix;
	int* tempProduct;

	int offset = 0;
	int rows;
	if(world_rank == 0) {
		readMatrix(&N,&M,&matrix);
		readVector(&vector);
		product = (int*) malloc(sizeof(int) * N);
		for(int i = 1; i < world_size; i++) {
			rows = N / world_size;
			if(i < N % world_size) rows++;
			offset += rows;
			MPI_Send(&offset, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&rows, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&M, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&matrix[offset*M], rows*M, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(vector, M, MPI_INT, i, 0, MPI_COMM_WORLD);
		}
		
		rows = N / world_size;
		if(N % world_size != 0) rows++;
		offset = 0;
		tempMatrix = matrix;
		tempProduct = product;
	} else {
		MPI_Recv(&offset, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&rows, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&M, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		
		tempMatrix = (int*) malloc(sizeof(int) * rows*M);
		vector = (int*) malloc(sizeof(int) * M);
		MPI_Recv(tempMatrix, rows*M, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(vector, M, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);		

		tempProduct = (int*) malloc(sizeof(int) * rows);
	}
	for(int i = 0; i < rows; i++) {
		tempProduct[i] = 0;
		for(int j = 0; j < M; j++) {
			tempProduct[i] += vector[j]*tempMatrix[i*M + j]; 
		}
	}

	if(world_rank == 0) {
		for(int i = 1; i < world_size; i++) {
			MPI_Recv(&offset, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			MPI_Recv(&rows, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			MPI_Recv(&product[offset], rows, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}
		ofstream productFile;
		productFile.open("product.txt");
		for(int i = 0; i < N; i++) {
			productFile << product[i] << " ";
		}
		productFile << endl;
		productFile.close();
	} else {
	cout << world_rank << endl;
		MPI_Send(&offset, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(&rows, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(tempProduct, rows, MPI_INT, 0, 0, MPI_COMM_WORLD);
	}
	
	if(world_rank == 0) {
		free(matrix);
		free(product);
	} else { 
		free(tempMatrix);
		free(tempProduct);
	}
	free(vector);

	MPI_Finalize();			
}
