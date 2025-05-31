#!/bin/bash

# Variables para controlar el tiempo de espera (en segundos)
WAIT_TIME=5

echo "Ejecutando mongosh en el router01 para mostrar dbs..."
docker compose exec router01 mongosh --port 27017 --eval "show dbs"
ROUTER_MONGOSH_STATUS=$?
if [ $ROUTER_MONGOSH_STATUS -ne 0 ]; then
  echo "Error al ejecutar mongosh para mostrar dbs (código de salida: $ROUTER_MONGOSH_STATUS). Saliendo."
  exit 1
else
  echo "mongosh ejecutado exitosamente para mostrar dbs."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Mostrando la información de sh.status() inicial..."
docker compose exec router01 mongosh --port 27017 --eval "sh.status()"
SH_STATUS_INITIAL=$?
if [ $SH_STATUS_INITIAL -ne 0 ]; then
  echo "Error al ejecutar sh.status() inicial (código de salida: $SH_STATUS_INITIAL). Saliendo."
  exit 1
else
  echo "Información de sh.status() inicial mostrada."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Ejecutando comandos dentro del contenedor router-01..."
docker exec -i router-01 bash <<EOF_BASH
echo "Ejecutando comandos de mongosh..."
mongosh --eval 'use uberto; db.mongodriver.createIndex({ id: "hashed" }); sh.shardCollection("uberto.mongodriver", { id: "hashed" })'

MONGOSH_STATUS=\$?
if [ \$MONGOSH_STATUS -ne 0 ]; then
  echo "Error al ejecutar comandos de mongosh (código de salida: \$MONGOSH_STATUS). Saliendo."
  exit 1
else
  echo "Comandos de mongosh ejecutados exitosamente."
fi

echo "Saliendo de la shell del contenedor router-01."
exit 0
EOF_BASH
ROUTER_EXEC_STATUS=$?
if [ $ROUTER_EXEC_STATUS -ne 0 ]; then
  echo "Error al ejecutar comandos dentro del contenedor router-01 (código de salida: $ROUTER_EXEC_STATUS). Saliendo."
  exit 1
else
  echo "Comandos dentro del contenedor router-01 ejecutados exitosamente."
fi
echo "Esperando $WAIT_TIME segundos..."
sleep $WAIT_TIME

echo "Mostrando la información de sh.status() final..."
docker compose exec router01 mongosh --port 27017 --eval "sh.status()"
SH_STATUS_FINAL=$?
if [ $SH_STATUS_FINAL -ne 0 ]; then
  echo "Error al ejecutar sh.status() final (código de salida: $SH_STATUS_FINAL). Saliendo."
  exit 1
else
  echo "Información de sh.status() final mostrada."
fi

echo "Configuración de Shard Key y Sharding completada."