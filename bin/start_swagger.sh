#docker pull swaggerapi/swagger-editor

echo "Ignore reference below:  It's on http://localhost:7081"

docker pull swaggerapi/swagger-editor:latest

docker run -d -p 7081:8080 swaggerapi/swagger-editor