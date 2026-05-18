import 'package:flutter/material.dart';

class UIExamples extends StatefulWidget {
  @override
  _UIExamplesState createState() => _UIExamplesState();
}

class _UIExamplesState extends State<UIExamples> 
    with TickerProviderStateMixin {
  
  late AnimationController animController;
  
  @override
  void initState() {
    super.initState();
    
    // GCI531: Animation trop longue - VIOLATION
    animController = AnimationController(
      duration: Duration(seconds: 8), // VIOLATION: > 5 secondes
      vsync: this
    );
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          // GCI31: Format d'image lourd - VIOLATION
          Image.asset('assets/images/logo.bmp'), // VIOLATION: BMP
          
          // GCI531: Animation complexe - VIOLATION
          Transform.rotate(
            angle: animController.value, // VIOLATION
            child: Container(),
          ),
        ],
      ),
    );
  }
}
