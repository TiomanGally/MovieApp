db.createUser(
    {
        user: "movieitUser",
        pwd: "movieitPassword",
        roles:[
            {
                role: "readWrite",
                db:   "movieitDb"
            }
        ]
    }
);