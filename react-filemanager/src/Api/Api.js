import config from './../config.js';

/**
 * Fetch API to list files from directory
 * @param {String} path
 * @returns {Object}
 */
export function list(path) {
    return fetch(config.url_list, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "hdfsPath": path
        })
    });};


/**
 * Fetch API to create a directory
 * @param {String} path
 * @param {String} directory
 * @returns {Object}
 */
export function createDirectory(path, directory) {
    return fetch(config.url_create_folder, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "hdfsPath": path === "/" ? path + directory : path + "/" + directory
        })
    });
};

/**
 * Fetch API to remove a file or folder
 * @param {String} path
 * @param {Array} filenames
 * @param {Boolean} recursive
 * @returns {Object}
 */
export function remove(path, filenames, recursive = true) {

    return fetch(config.url_remove, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "hdfsPath": path === "/" ? path + filenames[0] : path + "/" + filenames[0]
        })
    });
};

/**
 * Fetch API to copy files
 * @param {String} path
 * @param {Object<FileList>} fileList
 * @returns {Object}
 */
export function upload(path, fileList, formData = new FormData()) {
    [...fileList].forEach(f => {
        formData.append('file[]', f);
    });
    formData.append('path', path);
    
    console.log(path, fileList);

    return fetch(config.url_upload, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "hdfsPath": path === "/" ? path + fileList[0].name : path + "/" + fileList[0].name,
            "inodeType": "FILE",
            "hostPath": "/home/" + fileList[0].name
        })
    });
};


/**
 * Fetch API to get file body
 * @param {String} path
 * @returns {Object}
 */
export function getFileContent(path) {
    return fetch(config.url_get_content, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "hdfsPath": path,
            "inodeType": "FILE",
        })
    });
};

/**
 * Fetch API to move files
 * @param {String} path
 * @param {Array} filenames
 * @param {Boolean} recursive
 * @returns {Object}
 */
export function move(path, destination, filenames) {
    return fetch(config.url_move, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            path, destination, filenames
        })
    });
};

/**
 * Fetch API to move files
 * @param {String} path
 * @param {Array} filenames
 * @param {Boolean} recursive
 * @returns {Object}
 */
export function rename(path, destination) {
    return fetch(config.url_rename, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            path, destination
        })
    });
};

/**
 * Fetch API to copy files
 * @param {String} path
 * @param {Array} filenames
 * @param {Boolean} recursive
 * @returns {Object}
 */
export function copy(path, destination, filenames) {
    return fetch(config.url_copy, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            path, destination, filenames
        })
    });
};