import 'package:flutter/material.dart';

class OptimizedAPIExamples extends StatefulWidget {
  @override
  _OptimizedAPIExamplesState createState() => _OptimizedAPIExamplesState();
}

class _OptimizedAPIExamplesState extends State<OptimizedAPIExamples> {
  
  // GCI201: setState dans boucle - VIOLATION
  void badSetStateLoop(List<int> items) {
    for (var item in items) {
      setState(() { // VIOLATION: setState dans boucle
        // traitement
      });
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          // GCI202: ListView avec children - VIOLATION
          ListView(children: [ // VIOLATION: Utiliser ListView.builder
            Text('Item 1'),
            Text('Item 2'),
            Text('Item 3'),
          ]),
          
          // GCI532: Image réseau non optimisée - VIOLATION
          Image.network('https://example.com/large-image.jpg'), // VIOLATION
        ],
      ),
    );
  }
}
