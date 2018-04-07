var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'KeyboardService', 'coolMethod', [arg0]);
};

exports.start = function (success, error) {
    exec(success, error, 'KeyboardService', 'start', [arg0]);
};

exports.stop = function (success, error) {
    exec(success, error, 'KeyboardService', 'stop', [arg0]);
};
