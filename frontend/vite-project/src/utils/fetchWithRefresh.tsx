export const fetchWithRefresh = async (url: RequestInfo | URL, options = {}) => {
    let res = await fetch(url, {
        ...options,
        credentials: "include"
    });

    if (res.status === 401) {
        await fetch("http://localhost:8080/auth/refresh", {
            method: "POST",
            credentials: "include"
        });

        res = await fetch(url, {
            ...options,
            credentials: "include"
        });
    }


    return res;
};
