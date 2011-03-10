var module = {
    name : "Credit Card Identification",
    type: "response-processor"
};

// algorithm implementation borrowed from wikipedia
// todo: be more original


function luhncheck(str) {
  str = (str + '').replace(/\D+/g, '').split('').reverse();
  if (!str.length)
    return false;
  var t = 0, i;
  for (i = 0; i < str.length; i++) {
    str[i] = parseInt(str[i])
    t += i % 2 ? 2 * str[i] - (str[i] > 4 ? 9 : 0) : str[i];
  }
  return (t % 10) == 0;
}

function run()
{
  var regexp = /\b(?:\d[ -]*?){13,16}\b/gm;
  var cards = [];
  var res;
  
  while(res = regexp.exec(response.bodyAsString)) {
    if (luhncheck(res[0]))
    {
      cards.push(res[0]);
    }
  }
  if (cards.length)
  {
    model.alert("vpii-cc", { "output": cards.join("\n"), "resource": httpRequest.requestLine.uri, response: response });
  }
}
