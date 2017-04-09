#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <assert.h>

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
		// read file
		for(int i = 1; i < world_size; i++) {
			rows = N / world_size;
			if(i < N % world_size) rows++;
			offset += rows;
			MPI_Send(&offset, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&rows, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(&M, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(matrix[offset], rows*M, MPI_INT, i, 0, MPI_COMM_WORLD);
			MPI_Send(vector, M, MPI_INT, i, 0, MPI_COMM_WORLD);
		}
		
		rows = N / world_size;
		if(N % world_size != 0) rows++;
		offset = 0;
		tempMatrix = matrix[offset];
		tempProduct = product;
	} else {
		MPI_Recv(&offset, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&rows, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&M, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(tempMatrix, rows*M, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(vector, M, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);		
	}
	for(int i = 0; i < rows; i++) {
		for(int j = 0; j < M; i++) {
			tempProduct[i] += tempMatrix[i*M + j]; 
		}
	}
	if(world_rank == 0) {
		for(int i = 1; i < world_size; i++) {
			MPI_Recv(&offset, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			MPI_Recv(&rows, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			MPI_Recv(&product[offset], rows, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		}
	} else {
		MPI_Send(&offset, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(&rows, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(tempProduct, rows, MPI_INT, 0, 0, MPI_COMM_WORLD);
	}
	MPI_Finalize();			
}
