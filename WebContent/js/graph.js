  // create an array with nodes
  var nodes = new vis.DataSet([
    {id: "karim", label: 'Karim'},
    {id: "isma", label: 'Ismael'},
    {id: "yacine", label: 'Yacine'},
    {id: "asma", label: 'Asma'},
    {id: "hafsaa", label: 'Hafsaa'}
  ]);

  // create an array with edges
  var edges = new vis.DataSet([
    {from: "karim", to: "yacine"},
    {from: "karim", to: "isma"},
    {from: "isma", to: "yacine"},
    {from: "isma", to: "asma"},
    {from: "isma", to: "hafsaa"},
    {from: "asma", to: "karim"}
  ]);

  // create a network
  var container = document.getElementById('graph');
  var data = {
    nodes: nodes,
    edges: edges
  };
  var options = {};
  var network = new vis.Network(container, data, options);
