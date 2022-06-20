/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 28.03.2022
Projektname: BikerBoost
 */

//Aufgaben der Klasse: registrieren eines Users mit Vorname, Nachname, Username, Email und Passwort

const registerUser = () => {
    let formData = new FormData();

    formData.append("firstname",  document.getElementById("firstname").value);
    formData.append("lastname", document.getElementById("lastname").value);
    formData.append("username",  document.getElementById("username").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("password",  document.getElementById("password").value);


    fetch('/api/register',
        {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: formData
        })
        .then( response => {
            console.log(response)
            window.location.href = "/index.html";
        }).catch(err => console.log(err))

}