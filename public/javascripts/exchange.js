$(document).ready(function(){

    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 1200 - margin.left - margin.right,
        height = 600 - margin.top - margin.bottom;

    var parseDate = d3.time.format("%Y-%m-%d").parse;

    var x = d3.time.scale()
        .range([0, width]);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left");

    var line = d3.svg.line()
        .interpolate("basis")
        .x(function(d) { return x(d.date); })
        .y(function(d) { return y(d.exchangeRate); });

    var svg = d3.select("#graphContainer").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    var all_currency_rates = null

    //Draw the USD graph, and populate the options
    d3.json("/getNinetyDaysExchangeRates", function(error, data) {
        all_currency_rates = data
        for (currency in all_currency_rates) {
            $('#currency').append($('<option>' + currency + '</option>'));
        }
        drawGraph('USD');
        $('#currency').val('USD');
    });

    $('#currency').change(function(){
        d3.select('.y.axis').remove()
        d3.select('.line').remove()
        drawGraph($(this).val())
    });



    var drawGraph = function(currency){
        var rates = all_currency_rates[currency];
        var data = [];
        for (var date in rates){
            var d = {};
            d.date = parseDate(date.substring(0,10));
            d.exchangeRate = rates[date];
            data.push(d)
        }
        console.log(data)
        x.domain(d3.extent(data, function(d) { return d.date; }));
        y.domain(d3.extent(data, function(d) { return d.exchangeRate; }));

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Rate ("+ currency + ")");

        svg.append("path")
            .datum(data)
            .attr("class", "line")
            .attr("d", line);
    }
});