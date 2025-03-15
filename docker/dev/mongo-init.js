
db.createUser(
    {
        user: "mongouser",
        pwd: "mongopwd",
        roles: [
            {
                role: "readWrite",
                db: "myjobs"
            }
        ]
    }
);