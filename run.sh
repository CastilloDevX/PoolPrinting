#!/usr/bin/env bash
set -e
mkdir -p out

echo ">>> Hecho por Jose Manuel Castillo Queh - ITS 5A :D"
echo ">>> --------------------------------------------"

# Primer argumento: modo (sem para semaforos | mon para monitores | both para ambos)
MODE=${1:-both}

# Parámetros de ejecución (printers users jobsPerUser)
PRINTERS=${2:-3}
USERS=${3:-6}
JOBS=${4:-4}

javac -d out src/semaphore/*.java src/monitor/*.java
echo ">>> --------------------------------------------"
echo ">>> Parámetros: printers=$PRINTERS users=$USERS jobsPerUser=$JOBS"
echo ">>> --------------------------------------------"

if [[ "$MODE" == "sem" || "$MODE" == "both" ]]; then
  echo
  echo ">>> Ejecutando versión SEMÁFOROS"
  java -cp out semaphore.MainSemaphore $PRINTERS $USERS $JOBS
  echo ">>> --------------------------------------------"
fi

if [[ "$MODE" == "mon" || "$MODE" == "both" ]]; then
  echo
  echo ">>> Ejecutando versión MONITORES"
  java -cp out monitor.MainMonitor $PRINTERS $USERS $JOBS
  echo ">>> --------------------------------------------"
fi