#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// Evitar otimização do compilador
volatile int dummy = 0;

// -------- Algoritmos simulados --------

// Quadrático
void quadratico_melhor(int n) {
	int i;
    for (i = 0; i < n; i++) dummy++;
}
void quadratico_pior(int n) {
	int i, j;
    for ( i = 0; i < n; i++)
        for ( j = 0; j < n; j++) dummy++;
}

// Linear (simulando n log n)
void linear_melhor(int n) {
	int i;
    for (i = 0; i < n; i++) dummy++;
}
void linear_pior(int n) {
		int i, j;
    for (i = 0; i < n; i++)
        for (j = 1; j < n; j *= 2) dummy++;
}

// Logarítmico
void log_melhor(int n) {
    dummy++;
}
void log_pior(int n) {
		int i;
    for (i = 1; i < n; i *= 2) dummy++;
}

// Cúbico
void cubico_melhor(int n) {
		int i;
    for (i = 0; i < n; i++) dummy++;
}
void cubico_pior(int n) {
		int i, j, k;
    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++)
            for (k = 0; k < n; k++) dummy++;
}

// -------- Função auxiliar para medir tempo --------
double medirTempo(void (*func)(int), int n) {
    int r, repeticoes = 100; // repete várias vezes para dar tempo suficiente
    struct timespec ini, fim;

    clock_gettime(CLOCK_MONOTONIC, &ini);
    for (r = 0; r < repeticoes; r++) {
        func(n);
    }
    clock_gettime(CLOCK_MONOTONIC, &fim);

    // converte para segundos
    double tempo = (fim.tv_sec - ini.tv_sec) +
                   (fim.tv_nsec - ini.tv_nsec) / 1e9;

    return tempo / repeticoes; // tempo médio por execução
}

// -------- Execução automática --------
void executarAnalise(int escolha) {
    int n, inicio, fim, passo;
    void (*melhor)(int);
    void (*pior)(int);
    const char* nome;

    switch (escolha) {
        case 1: // Quadrático
            inicio = 100; fim = 5000; passo = 1000;
            melhor = quadratico_melhor; pior = quadratico_pior;
            nome = "O(n^2) Quadrático";
            break;
        case 2: // Linear (n log n)
            inicio = 1000; fim = 100000; passo = 20000;
            melhor = linear_melhor; pior = linear_pior;
            nome = "O(n log n) Linear";
            break;
        case 3: // Logarítmico
            inicio = 100000; fim = 10000000; passo = 2000000;
            melhor = log_melhor; pior = log_pior;
            nome = "O(log n) Logarítmico";
            break;
        case 4: // Cúbico
            inicio = 10; fim = 200; passo = 50;
            melhor = cubico_melhor; pior = cubico_pior;
            nome = "O(n^3) Cúbico";
            break;
        default:
            printf("Opção inválida!\n");
            return;
    }

    printf("\n=== Resultados para %s ===\n", nome);
    printf("%-10s | %-15s | %-15s\n", "N", "Melhor Caso (s)", "Pior Caso (s)");
    printf("------------------------------------------------------\n");

    for (n = inicio; n <= fim; n += passo) {
        double t_melhor = medirTempo(melhor, n);
        double t_pior = medirTempo(pior, n);
        printf("%-10d | %-15.9f | %-15.9f\n", n, t_melhor, t_pior);
    }
}

// -------- Main --------
int main() {
    int escolha;
    printf("=== ANALISE EMPIRICA DE ALGORITMOS ===\n");
    printf("[1] Quadratico O(n^2)\n");
    printf("[2] Linear O(n log n)\n");
    printf("[3] Logaritmico O(log n)\n");
    printf("[4] Cubico O(n^3)\n");
    printf("Escolha: ");
    scanf("%d", &escolha);

    executarAnalise(escolha);

    printf("\nAnalise concluida.\n");
}
