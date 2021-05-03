const pmx = require('pmx');
const queue = require('queue');
const q = new queue();
const SerialPort = require('serialport');
const Readline = require('@serialport/parser-readline')
const port = new SerialPort("/dev/ttyUSB3", {
   baudRate: 115200,
   dataBits: 8,
   parity: 'none',
});
const parser = new Readline();
port.pipe(parser);

var results = [];

var band = "2";
var channel = "18600";
var tx = "84";
var command = '';
var expected_result = '';
var pause = '';

var bands = [
  [1, 18000, 18599],
  [2, 18600, 19199],
  [3, 19200, 19949],
  [4, 19950, 20399],
  [5, 20400, 20649],
  [7, 20750, 21449],
  [8, 21450, 21799],
  [10, 22150, 22749],
  [12, 23010, 23179],
  [13, 23180, 23279],
  [14, 23280, 23379],
  [17, 23730, 23849],
  [20, 24150, 24449],
  [25, 26040, 26689],
  [26, 26690, 27039],
  [28, 27210, 27659],
  [34, 36200, 36349],
  [38, 37750, 38249],
  [39, 38250, 38649],
  [40, 38650, 39649],
  [41, 39650, 41589],
  [66, 131972, 132671],
];

function getBandInfo(band) {
  for (var bandindex in bands) {
    if (bands[bandindex][0] == band) {
      return bands[bandindex];
    }
  };
};

console.log('port is now open');

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

parser.on('data', function(line) {
  if (!command) {
    command = line;
    expected_result = results.shift();
  } else {
    if (line.trim() == expected_result[0]) {
      command = '';
      if (expected_result[1]) { expected_result[1]();};
    };
  };
});  

port.on("open", function () {
  console.log('Serial communication open');

  q.push(function(){
    return new Promise(function (resolve, reject) {
      results.push(['OK', resolve])
      port.write("AT\r\n");
    })
  });

  q.push(function(){
    return new Promise(function (resolve, reject) {
      results.push(['OK', resolve]);
      port.write("ATZ\r\n");
      enable_test_mode(true);
    })
  });

  q.start(function (err) {
    if (err) throw err
    console.log('all done:', results)
  })
});

function enable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push(['OK', resolve]);
      port.write("AT+QRFTESTMODE=1\r\n");
      set_transmit_specs();
    })
  })
};

function disable_test_mode() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push(['OK', resolve]);
      port.write("AT+QRFTESTMODE=0\r\n");
    })
  });
};

function set_transmit_specs() {
  q.push(function() {
    return new Promise(function (resolve, reject) {
      results.push(['OK', resolve]);
      port.write("AT+QRFTEST=\"LTE BAND" + band + "\"," + channel + ",\"ON\"," + tx + ",1\r\n");
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

pmx.action('band', function(param, reply) {
  bandinfo = getBandInfo(param);
  console.log(bandinfo);
  if (!bandinfo) {
    response = 'Unsupported band';
  } else {
    response = 'success';
    band = bandinfo[0];
    channel = bandinfo[1];
    set_transmit_specs();
  };

  reply({ answer : response });
});

pmx.action('channel', function(param, reply) {
  bandinfo = getBandInfo(band);
  if (param < bandinfo[1] || param > bandinfo[2]) {
    response = 'Invalid channel for this band';
  } else {
    response = 'success';
    channel = param;
    set_transmit_specs();
  };

  reply({ answer : response });
});

pmx.action('power', function(param, reply) {
  if (param < 1 || param > 84) {
    response = 'Invalid power setting';
  } else {
    response = 'success';
    tx = param;
    set_transmit_specs();
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
     console.log("lte paused");
   } else {
     console.log("lte running: band " + band + " channel " + channel +" txpower " + tx);
   };
}, 10000);
