#!/bin/bash
#La idea seria ejecutar este script una unica vez cuando hago el pull por primera vez
#Para configurar los contenedores... este script NO posee la configuracion de la shard key
# Variables para controlar el tiempo de espera (en segundos)
WAIT_TIME=5

echo "Iniciando los contenedores Docker..."
docker compose up -d
if [ $? -ne 0 ]; then
  echo "Error al iniciar los contenedores Docker. Saliendo."
  exit 1
fi

echo "Esperando $WAIT_TIME segundos para que los contenedores se inicien..."
sleep $WAIT_TIME

echo "Ejecutando el script de inicialización del Config Server 01..."
docker compose exec configsvr01 sh -c "mongosh < /scripts/init-configserver.js"
CONFIG_SERVER_STATUS=$?
if [ $CONFIG_SERVER_STATUS -ne 0 ]; then
  echo "Error al ejecutar el script de inicialización del Config Server 01 (código de salida: $CONFIG_SERVER_STATUS). Saliendo."
  exit 1
else
  echo "Script de inicialización del Config Server 01 ejecutado exitosamente."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Ejecutando el script de inicialización del Shard 01 (réplica a)..."
docker compose exec shard01-a sh -c "mongosh < /scripts/init-shard01.js"
SHARD01_STATUS=$?
if [ $SHARD01_STATUS -ne 0 ]; then
  echo "Error al ejecutar el script de inicialización del Shard 01 (código de salida: $SHARD01_STATUS). Saliendo."
  exit 1
else
  echo "Script de inicialización del Shard 01 ejecutado exitosamente."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Ejecutando el script de inicialización del Shard 02 (réplica a)..."
docker compose exec shard02-a sh -c "mongosh < /scripts/init-shard02.js"
SHARD02_STATUS=$?
if [ $SHARD02_STATUS -ne 0 ]; then
  echo "Error al ejecutar el script de inicialización del Shard 02 (código de salida: $SHARD02_STATUS). Saliendo."
  exit 1
else
  echo "Script de inicialización del Shard 02 ejecutado exitosamente."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Ejecutando el script de inicialización del Router 01 (mongos)..."
docker compose exec router01 sh -c "mongosh < /scripts/init-router.js"
ROUTER_STATUS=$?
if [ $ROUTER_STATUS -ne 0 ]; then
  echo "Error al ejecutar el script de inicialización del Router 01 (código de salida: $ROUTER_STATUS). Saliendo."
  exit 1
else
  echo "Script de inicialización del Router 01 ejecutado exitosamente."
fi

echo "Configuración de Docker completada."