var exec = require('cordova/exec');

exports.getCurrentPosition = function(successFn, failureFn) {
    exec(successFn, failureFn, 'BaiduLocation', 'getCurrentPosition', []);
};
