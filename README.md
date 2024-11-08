###
POST http://localhost:8080/api/v1/student
Content-Type: application/json

{
"name": "Quandale",
"email": "quandale.dingle@gmail.com",
"dob": "1969-04-20"
}


###
PUT http://localhost:8080/api/v1/student/1?name=Juan&email=juan@gmail.com
Content-Type: application/json


###
DELETE http://localhost:8080/api/v1/student/1

