const pmx = require('pmx');
const queue = require('queue');
const q = new queue();
const execSync = require('child_process').execSync;

var channel = "7";
var tx = "20";
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
  code = execSync('rfkill unblock all ');
}, 1000);
setTimeout(function () {
  try {
    code = execSync('calibrator wlan0 plt power_mode on');
  } catch(err) {
    code = execSync('calibrator wlan0 plt power_mode off');
    code = execSync('calibrator wlan0 plt power_mode on');
  }
}, 1000);
configure_test();
enable_test_mode();

q.start(function (err) {
  if (err) throw err
  console.log('all done:', results)
})

function enable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('calibrator wlan0 wl18xx_plt start_tx 200 19 3000 0 0 1 0 0 00:11:22:33:44:55 01:02:03:04:05:06 0');
        resolve();
      }, 1000);
    })
  });
};

function disable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('calibrator wlan0 wl18xx_plt stop_tx');
        resolve();
      }, 1000);
    });
  });
};

function configure_test() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('calibrator wlan0 wl18xx_plt stop_tx');
        resolve();
      }, 1000);
    });
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        code = execSync('calibrator wlan0 wl18xx_plt tune_channel ' + channel + ' 0 1');
        resolve();
      }, 1000);
    })
  });
  q.push(function() {
    return new Promise(function (resolve, reject) {
      setTimeout(function () {
        txpower = tx * 1000;
        console.log("txpower to " + txpower);
        code = execSync('calibrator wlan0 wl18xx_plt set_tx_power ' + txpower + ' 0 0 ' + channel + ' 0 0 0 1 0 0 0 0');
        resolve();
      }, 1000);
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
  pause = '';
  reply({ answer : 'success' });
});

pmx.action('channel', function(param, reply) {
  if (param < 1 || param > 14) {
    response = 'Invalid channel';
  } else { 
    response = 'success';
    channel = param;
    configure_test();
    enable_test_mode(); 
  };
    
  reply({ answer : response });
});

pmx.action('power', function(param, reply) {
  if (param < 1 || param > 20) {
    response = 'Invalid power setting';
  } else { 
    response = 'success';
    tx = param;
    configure_test();
    enable_test_mode(); 
  };
    
  reply({ answer : response });
});

process.on('SIGINT', function() {
  console.info('SIGINT signal received.')
  setTimeout(function () {
     code = execSync('calibrator wlan0 plt power_mode off');
  }, 1000);
  setTimeout(function () {
    console.log('exiting for real');
    return process.exit(0);
  }, 5000);
});

setInterval(function () {
   if (pause) {
     console.log("wifi paused");
   } else {
     console.log("wifi running: channel " + channel +" txpower " + tx);
   };
}, 10000);

