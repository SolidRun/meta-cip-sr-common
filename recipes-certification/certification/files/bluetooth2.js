const pmx = require('pmx');
const queue = require('queue');
const q = new queue();
const SerialPort = require('serialport');
const Readline = require('@serialport/parser-readline')
const port = new SerialPort("/dev/ttymxc2", {
   baudRate: 115200,
   dataBits: 8,
   parity: 'none',
});
const parser = new Readline();
port.pipe(parser);

var results = [];

var channel = "40";
var tx = "0";
var command = '';
var expected_result = '';
var pause = '';

console.log('port is now open');

q.concurrency = 1;
q.autostart = 1;
q.timeout = 500;

// get notified when jobs complete
q.on('success', function (result, job) {
  console.log('job finished processing:', job.toString().replace(/\n/g, ''))
})

q.on('timeout', function (next, job) {
  console.log('job timed out:', job.toString().replace(/\n/g, ''));
  next();
})

parser.on('data', function(line) {
  console.log(line);
  if (!command) {
    command = line;
    expected_result = results.shift();
  } else {
    if (line.trim() == expected_result[0]) {
      console.log(">" +expected_result[0] +" >" +line);
      command = '';
      if (expected_result[1]) { expected_result[1]();};
    };
  };
});  

port.on("open", function () {
  console.log('Serial communication open');

  set_start_channel();
  set_transmit_power();
  enable_test_mode();

  q.start(function (err) {
    if (err) throw err
    console.log('all done:', results)
  })
});

function enable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push(['TX carrier', resolve]);
      port.write("c");
    })
  })
};

function disable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push(['Cancelling test', resolve]);
      port.write("e");
    })
  });
};

function set_start_channel() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push([channel, resolve]);
      port.write("a\n" + channel +"\n");
    })
  });
};

function set_transmit_power() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push([tx, resolve]);
      port.write("p\n" + tx +"\n");
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
  if (param < 0 || param > 80) {
    response = 'Invalid channel';
  } else {
    response = 'success';
    channel = param;
    set_start_channel();
  };

  reply({ answer : response });
});

pmx.action('power', function(param, reply) {
  if (param < 0 || param > 7) {
    response = 'Invalid power setting';
  } else {
    response = 'success';
    tx = param;
    set_transmit_power();
  };

  reply({ answer : response });
});

process.on('SIGINT', function() {
  console.info('SIGINT signal received.')
  disable_test_mode();  
  setTimeout(function () {
    console.log('exiting for real');
    return process.exit(0);
  }, 1000);
});

setInterval(function () {
   if (pause) {
     console.log("bluetooth2 paused");
   } else {
     console.log("bluetooth2 running channel " +channel +" txpower " +tx);
   };
}, 10000);
