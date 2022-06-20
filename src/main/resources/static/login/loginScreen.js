/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 28.03.2022
Projektname: BikerBoost
 */

//Aufgaben der Klasse: einloggen des Users über Passwort und Email

const loginUser = () => {
    let formData = new FormData();

    formData.append("email",  document.getElementById("email").value);
    formData.append("password", document.getElementById("passwd").value);

    fetch('/api/login',
        {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: formData
        })
        .then(response => {
            console.log(response)
            if(response.status == 200){
                window.location.href = "/templates/user.html"
            }else {
                //TODO: user informieren, dass falscher login
            }
        }).catch(err => console.log(err))
}