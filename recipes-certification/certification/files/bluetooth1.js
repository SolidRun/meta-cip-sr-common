const pmx = require('pmx');
const queue = require('queue');
const q = new queue();
const execSync = require('child_process').execSync;

var channel = "3";
var tx = "7";
var command = '';
var expected_result = '';
var pause = '';

q.concurrency = 1;
q.autostart = 1;

// get notified when jobs complete
q.on('success', function (result, job) {
  console.log('job finished processing:', job.toString().replace(/\n/g, ''))
})

q.on('timeout', function (next, job) {
  console.log('job timed out:', job.toString().replace(/\n/g, ''));
  next();
})

setTimeout(function () {
  code = execSync('hciconfig 0 up');
}, 1000);
setTimeout(function () {
  code = execSync('hciconfig 0 noscan');
}, 1000);
enable_test_mode();
configure_test();

q.start(function (err) {
  if (err) throw err
  console.log('all done:', results)
})

function enable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x3f 0x10c 0x00 0x00 0xff 0xff 0xff 0xff 0xff 0x64');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x01 0x001 0x33 0x8b 0x9e 0x04 0x00');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x03 0x001a 0x00');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x3f 0x01fb 0x01 0xff 0x00 0x00 0x00 0x00 0x01');
        resolve();
      }, 1000);
    })
  });
};

function disable_test_mode() {
  console.log("disabling test mode");
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x3f 0x0301 0x44 0x40 0x01 0x20 0x10 0x00');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x3f 0x0301 0x48 0x40 0x01 0x20 0x14 0x00');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('hcitool cmd 0x3f 0x0301 0x0c 0x90 0x01 0x20 0x00 0x00');
        resolve();
      }, 1000);
    })
  });
};

function configure_test() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        frequency = 2401 + Number(channel);
        hexchannel = Number(frequency).toString(16).slice(-2);
        code = execSync('hcitool cmd 0x3f 0x01ca 0x' +hexchannel +' 0x09 0x01 0x00 0x0' +tx +' 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00');
        resolve();
      }, 3000);
    })
  });
};

pmx.action('pause', function(reply) {
  disable_test_mode();
  pause = true;
  reply({ answer : 'success' });
});

pmx.action('resume', function(reply) {
  enable_test_mode();
  configure_test();
  pause = '';
  reply({ answer : 'success' });
});

pmx.action('channel', function(param, reply) {
  if (param < 1 || param > 79) {
    response = 'Invalid channel';
  } else {
    response = 'success';
    channel = param;
    configure_test();
  };

  reply({ answer : response });
});

pmx.action('power', function(param, reply) {
  if (param < 0 || param > 7) {
    response = 'Invalid power setting';
  } else {
    response = 'success';
    tx = param;
    configure_test();
  };

  reply({ answer : response });
});

process.on('SIGINT', function() {
  console.info('SIGINT signal received.')
  disable_test_mode();
  setTimeout(function () {
    console.log('exiting for real');
    return process.exit(0);
  }, 5000);
});

setInterval(function () {
   if (pause) {
     console.log("bluetooth1 paused");
   } else {
     console.log("bluetooth1 running channel " +channel +" txpower " +tx);
   };
}, 10000);

