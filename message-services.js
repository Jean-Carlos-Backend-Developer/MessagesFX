const express = require('express');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');
const sha256 = require('sha256');
const bodyParser = require('body-parser');
const secretWord = "DAMsecret";
const fs = require('fs');
const { type } = require('os');

//Crear esquema User
let userSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        minlength: 1,
        trim: true,
        unique: true,
        match: /^[a-zA-Z0-9]+$/
    },
    password: {
        type: String,
        required: true,
        minlength: 1
    },
    image: {
        type: String,
        required: true
    }
});
let User = mongoose.model('users', userSchema);

//Crear esquema Message
let messageSchema = new mongoose.Schema({
    from: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    to: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    message: {
        type: String,
        required: true,
        trim: true,
        minlength: 1
    },
    image: {
        type: String
    },
    sent: {
        type: String,
        required: true,
        trim: true,
        minlength: 10
    }
});

let Message = mongoose.model('messages', messageSchema);

mongoose.Promise = global.Promise;
//Conectarse a la base de datos message-services
mongoose.connect('mongodb://localhost:27017/message-services');

let app = express();
app.use(express.json());

//Función para genera el token
let generateToken = name => {
    let token = jwt.sign({ name: name }, secretWord,
        { expiresIn: "1y" });
    return token;
}

//Crear usuario nuevo
app.post('/register', (req, res) => {

    //Crear archivo que contenga la imagen
    const filePath = `img/${Date.now()}.jpg`;
    fs.writeFileSync(filePath, Buffer.from(req.body.image, 'base64'));

    let newUser = new User({
        name: req.body.name,
        password: sha256(req.body.password),
        image: filePath
    });

    newUser.save().then(result => {
        let data = { error: false, result: result };
        res.send({ "ok": true });
    }).catch(error => {
        console.error(error);
        res.send({ ok: false, error: "User couldn't be registered" });
    });
});

//Loggin
app.post('/login', (req, res) => {
    let userClient = {
        name: req.body.name,
        password: sha256(req.body.password)
    };
    //Buscar al usuario en la colección
    User.findOne({ name: userClient.name, password: userClient.password }).then(data => {
        //Generar token si el usuario es válido
        if (data != null) {
            let token = generateToken(userClient.name);
            let result = { ok: true, token: token, name: data.name, image: data.image, id: data._id };
            res.send(result);
            // User not found, generate error message
        } else {
            //Usuario no encontrado, generar mensaje de error
            let result = { ok: false, error: "User or password incorrect" };
            res.send(result);
        }
    }).catch(error => {
        //Error buscnado el usuario
        let result = { ok: false, errorMessage: "Error trying to validate user" };
        res.send(result);
    });
});

//Obtener la lista de usuarios
app.get('/users', (req, res) => {
    let token = req.headers['authorization'];
    jwt.verify(token, secretWord, (err, decoded) => {
        if (err) {
            res.send({ error: true, errorMessage: err.message }); //Ver el mensaje de error específico
        } else {
            User.find().then(result => {
                res.send({ ok: true, users: result });
            })
        }
    });
});

//Actualizar la imagen del avatar de un usuario
app.put('/users/:id', (req, res) => {
    //Obtener los datos del usuario durante el proceso de autenticación
    User.findById(req.params.id).then(user => {
        //Generar un nuevo archivo ".jpg" donde se guardará la nueva imagen de avatar
        const filePath = `img/${Date.now()}.jpg`;
        fs.writeFileSync(filePath, Buffer.from(req.body.image, 'base64'));

        //Actualizar la imagen del avatar del usuario
        user.image = filePath;
        user.save().then(user => {
            //Actualizado
            res.send({ ok: true });
        }).catch(error => {
            //Error al actualizar
            res.send({ ok: false, error: "Error updating user: " + req.params.id });
        });
    }).catch(error => {
        //Usuario no encontrado
        res.send({ ok: false, error: "User not found: " + req.params.id });
    });
});

//Obtener la lista mensajes enviados al usuario que hace la solicitud
app.get('/users', (req, res) => {
    let token = req.headers['authorization'];
    jwt.verify(token, secretWord, (err, decoded) => {
        if (err) {
            res.send({ error: true, errorMessage: err.message }); //Ver el mensaje de error específico
        } else {
            User.find().then(result => {
                res.send({ ok: true, users: result });
            })
        }
    });
});

//Obtener todos los mensajes enviados al usuario
app.get('/messages', (req, res) => {
    let token = req.headers['authorization'];
    jwt.verify(token, secretWord, (err, decoded) => {
        if (err) {
            res.send({ error: true, errorMessage: err.message }); //Ver el mensaje de error específico
        } else {
            User.findOne({ name: decoded.name }).then(user => {
                Message.find({ to: user._id }).then(messages => {
                    res.send({ ok: true, messages: messages });
                }).catch(error => {
                    res.send({ ok: false, error: "Error getting messages for user: " + user._id });
                });
            }).catch(error => {
                res.send({ ok: false, error: "Error finding user: " + decoded.name });
            });
        }
    });
});

//Enviar mensajes al usuario especificado en el id
app.post('/messages/:toUserId', (req, res) => {
    let token = req.headers['authorization'];
    jwt.verify(token, secretWord, (err, decoded) => {
        if (err) {
            res.send({ error: true, errorMessage: err.message }); //Ver el mensaje de error específico
        } else {
            let filePath = null;
            //Verificar si la imagen existe antes de intentar escribir el archivo
            if (req.body.image) {
                filePath = `img/${Date.now()}.jpg`;
                fs.writeFileSync(filePath, Buffer.from(req.body.image, 'base64'));
            }

            //Obtenemos el nombre del ususario que manda el mensaje
            User.findOne({ name: decoded.name }).then(user => {

                let newMessage = new Message({
                    from: user._id,
                    to: req.params.toUserId,
                    message: req.body.message,
                    image: filePath,
                    sent: new Date().toISOString()
                });

                newMessage.save().then(result => {
                    res.send({ ok: true, message: result });
                }).catch(error => {
                    res.send({ ok: false, error: "Error sending message to user: " + req.params.toUserId });
                });
            }).catch(error => {
                res.send({ ok: false, error: "Error finding user: " + decoded.name });
            });
        }
    });
});


//Eliminar un mensaje que coincida con un id
app.delete('/messages/:id', (req, res) => {
    Message.findByIdAndDelete(req.params.id).then(result => {
        if (result) {
            res.send({ ok: true });
        } else {
            res.send({ ok: false, error: "No message found with id: " + req.params.id });
        }
    }).catch(error => {
        res.send({ ok: false, error: "Error deleting message: " + req.params.id });
    });
});

app.use(express.static('public'));
app.use('/img', express.static(__dirname + '/img'));
app.listen(8080);
